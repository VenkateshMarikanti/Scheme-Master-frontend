<?php
header("Content-Type: application/json");
include "db.php";

if (!isset($_GET['user_id'])) {
    echo json_encode([
        "status" => "error",
        "message" => "user_id required"
    ]);
    exit;
}

$user_id = intval($_GET['user_id']);

$stmt = mysqli_prepare(
    $conn,
    "SELECT user_id, name, email, phone, caste
     FROM users 
     WHERE user_id = ?"
);

mysqli_stmt_bind_param($stmt, "i", $user_id);
mysqli_stmt_execute($stmt);
$result = mysqli_stmt_get_result($stmt);

if ($row = mysqli_fetch_assoc($result)) {
    echo json_encode([
        "status" => "success",
        "data" => $row
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "User not found"
    ]);
}
?>
