<?php
header("Content-Type: application/json");
include "db.php";

$data = json_decode(file_get_contents("php://input"), true);
$email = trim($data['email'] ?? '');

if ($email == "") {
    echo json_encode([
        "status" => "error",
        "message" => "Email required"
    ]);
    exit;
}

$check = mysqli_prepare($conn, "SELECT * FROM users WHERE email=?");
mysqli_stmt_bind_param($check, "s", $email);
mysqli_stmt_execute($check);
$result = mysqli_stmt_get_result($check);

if (mysqli_num_rows($result) == 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Email not registered"
    ]);
    exit;
}

$token = bin2hex(random_bytes(32));
$expiry = date("Y-m-d H:i:s", strtotime("+15 minutes"));

$insert = mysqli_prepare(
    $conn,
    "INSERT INTO password_resets (email, token, expires_at) VALUES (?, ?, ?)"
);
mysqli_stmt_bind_param($insert, "sss", $email, $token, $expiry);
mysqli_stmt_execute($insert);

/*
 In real apps → send email
 For now → return token
*/
echo json_encode([
    "status" => "success",
    "message" => "Reset link generated",
    "reset_token" => $token
]);
?>
