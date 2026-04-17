<?php
header("Content-Type: application/json");
include "db.php";

$user_id = $_GET['user_id'] ?? '';

if ($user_id == '') {
    echo json_encode([
        "status" => "error",
        "message" => "user_id is required"
    ]);
    exit;
}

/*
  Logic:
  1. Get documents uploaded by user
  2. Compare with required documents for schemes
*/

$query = "
SELECT DISTINCT sd.document_type
FROM scheme_documents sd
LEFT JOIN user_documents ud
ON sd.document_type = ud.document_type
AND ud.user_id = ?
WHERE ud.document_type IS NULL
";

$stmt = $conn->prepare($query);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$missing_documents = [];

while ($row = $result->fetch_assoc()) {
    $missing_documents[] = $row['document_type'];
}

echo json_encode([
    "status" => "success",
    "missing_documents" => $missing_documents
]);
