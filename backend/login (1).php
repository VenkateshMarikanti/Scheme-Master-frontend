<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

// include database connection
include "db.php";

// read JSON input
$data = json_decode(file_get_contents("php://input"), true);

// check required fields
if (!isset($data['email']) || !isset($data['password'])) {
    echo json_encode([
        "status" => "error",
        "message" => "Email and password are required"
    ]);
    exit;
}

$email = trim($data['email']);
$password = trim($data['password']);

// prepare SQL
$sql = "SELECT user_id, name, email, password FROM users WHERE email = ?";
$stmt = mysqli_prepare($conn, $sql);

if (!$stmt) {
    echo json_encode([
        "status" => "error",
        "message" => "Database error"
    ]);
    exit;
}

mysqli_stmt_bind_param($stmt, "s", $email);
mysqli_stmt_execute($stmt);
$result = mysqli_stmt_get_result($stmt);

// check user exists
if (mysqli_num_rows($result) == 1) {

    $user = mysqli_fetch_assoc($result);

    // verify password
    if (password_verify($password, $user['password'])) {

        echo json_encode([
            "status" => "success",
            "message" => "Login successfully",
            "user" => [
                "user_id" => $user['user_id'],
                "name" => $user['name'],
                "email" => $user['email']
            ]
        ]);

    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Invalid password"
        ]);
    }

} else {
    echo json_encode([
        "status" => "error",
        "message" => "Email not registered"
    ]);
}

mysqli_stmt_close($stmt);
mysqli_close($conn);
?>
