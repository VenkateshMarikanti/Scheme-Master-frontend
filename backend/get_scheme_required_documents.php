<?php
header("Content-Type: application/json");
require "db.php";

$scheme_id = $_GET['scheme_id'] ?? '';

if ($scheme_id == '') {
    echo json_encode([
        "status" => "error",
        "message" => "scheme_id is required"
    ]);
    exit;
}

$stmt = $conn->prepare(
    "SELECT document_type 
     FROM scheme_documents 
     WHERE scheme_id = ?"
);
$stmt->bind_param("i", $scheme_id);
$stmt->execute();

$result = $stmt->get_result();
$documents = [];

while ($row = $result->fetch_assoc()) {
    $documents[] = $row['document_type'];
}

echo json_encode([
    "status" => "success",
    "required_documents" => $documents
]);
