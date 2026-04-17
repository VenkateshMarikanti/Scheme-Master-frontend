<?php
header("Content-Type: application/json");
require "db.php";

$user_id = $_POST['user_id'] ?? '';
$name    = $_POST['name'] ?? '';
$phone   = $_POST['phone'] ?? '';
$caste   = $_POST['caste'] ?? '';

if ($user_id == "" || $name == "" || $phone == "" || $caste == "") {
    echo json_encode([
        "status" => "error",
        "message" => "All fields are required"
    ]);
    exit;
}

$stmt = $conn->prepare(
    "UPDATE users SET name = ?, phone = ?, caste = ? WHERE user_id = ?"
);
$stmt->bind_param("sssi", $name, $phone, $caste, $user_id);

if ($stmt->execute()) {
    echo json_encode([
        "status" => "success",
        "message" => "Profile updated successfully"
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Profile update failed"
    ]);
}
?>
