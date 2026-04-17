<?php
header("Content-Type: application/json");
include "db.php";

if (!isset($_GET['user_id'])) {
    echo json_encode(["status"=>"error","message"=>"User ID required"]);
    exit;
}

$user_id = $_GET['user_id'];

$query = "SELECT document_id, document_type, file_name, status, uploaded_at
          FROM documents
          WHERE user_id = ?";

$stmt = mysqli_prepare($conn, $query);
mysqli_stmt_bind_param($stmt, "i", $user_id);
mysqli_stmt_execute($stmt);
$result = mysqli_stmt_get_result($stmt);

$documents = [];

while ($row = mysqli_fetch_assoc($result)) {
    $documents[] = $row;
}

echo json_encode([
    "status"=>"success",
    "data"=>$documents
]);
?>
