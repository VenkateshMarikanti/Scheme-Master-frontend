<?php
header("Content-Type: application/json");
include "db.php";

$data = json_decode(file_get_contents("php://input"), true);

$token = $data['token'] ?? '';
$newPassword = $data['new_password'] ?? '';

if ($token == "" || $newPassword == "") {
    echo json_encode([
        "status" => "error",
        "message" => "Token and new password required"
    ]);
    exit;
}

$check = mysqli_prepare(
    $conn,
    "SELECT * FROM password_resets WHERE token=? AND expires_at > NOW()"
);
mysqli_stmt_bind_param($check, "s", $token);
mysqli_stmt_execute($check);
$result = mysqli_stmt_get_result($check);

if (mysqli_num_rows($result) == 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid or expired token"
    ]);
    exit;
}

$row = mysqli_fetch_assoc($result);
$email = $row['email'];

$hashedPassword = password_hash($newPassword, PASSWORD_DEFAULT);

$update = mysqli_prepare(
    $conn,
    "UPDATE users SET password=? WHERE email=?"
);
mysqli_stmt_bind_param($update, "ss", $hashedPassword, $email);
mysqli_stmt_execute($update);

mysqli_query($conn, "DELETE FROM password_resets WHERE email='$email'");

echo json_encode([
    "status" => "success",
    "message" => "Password reset successful"
]);
?>
