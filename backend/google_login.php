<?php
include "db.php";
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"), true);

$email = $data['email'] ?? '';
$name = $data['name'] ?? '';
$google_id = $data['google_id'] ?? '';

if ($email == '' || $google_id == '') {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid Google data"
    ]);
    exit;
}

// Check if user exists
$check = mysqli_prepare($conn, "SELECT user_id FROM users WHERE email=?");
mysqli_stmt_bind_param($check, "s", $email);
mysqli_stmt_execute($check);
$result = mysqli_stmt_get_result($check);

if (mysqli_num_rows($result) > 0) {
    $user = mysqli_fetch_assoc($result);
    echo json_encode([
        "status" => "success",
        "user_id" => $user['user_id'],
        "message" => "Login successful"
    ]);
} else {
    // Register new Google user
    $insert = mysqli_prepare(
        $conn,
        "INSERT INTO users (name, email, google_id) VALUES (?, ?, ?)"
    );
    mysqli_stmt_bind_param($insert, "sss", $name, $email, $google_id);
    mysqli_stmt_execute($insert);

    echo json_encode([
        "status" => "success",
        "user_id" => mysqli_insert_id($conn),
        "message" => "Registered via Google"
    ]);
}
?>
