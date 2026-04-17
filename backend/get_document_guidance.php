<?php
header("Content-Type: application/json");
include "db.php";

$document_name = $_POST['document_name'] ?? '';

if ($document_name == '') {
    echo json_encode([
        "status" => "error",
        "message" => "document_name is required"
    ]);
    exit;
}

$stmt = $conn->prepare(
    "SELECT guidance, issuing_authority, required_for
     FROM document_guidance
     WHERE document_name = ?"
);

$stmt->bind_param("s", $document_name);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows == 0) {
    echo json_encode([
        "status" => "error",
        "message" => "No guidance found for this document"
    ]);
    exit;
}

$data = $result->fetch_assoc();

echo json_encode([
    "status" => "success",
    "data" => $data
]);
