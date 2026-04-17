<?php
header("Content-Type: application/json");
require "db.php";

/**
 * POST:
 * document_id
 */

// 1️⃣ Validate input
if (!isset($_POST['document_id']) || empty($_POST['document_id'])) {
    echo json_encode([
        "status" => "error",
        "message" => "document_id is required"
    ]);
    exit;
}

$document_id = intval($_POST['document_id']);

// 2️⃣ Fetch document using CORRECT column name
$stmt = $conn->prepare(
    "SELECT file_path FROM user_documents WHERE document_id = ?"
);
$stmt->bind_param("i", $document_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Document not found"
    ]);
    exit;
}

$row = $result->fetch_assoc();
$file_path = $row['file_path'];

// 3️⃣ Delete file from uploads folder
if (!empty($file_path) && file_exists($file_path)) {
    unlink($file_path);
}

// 4️⃣ Delete database record
$delete = $conn->prepare(
    "DELETE FROM user_documents WHERE document_id = ?"
);
$delete->bind_param("i", $document_id);

if ($delete->execute()) {
    echo json_encode([
        "status" => "success",
        "message" => "Document deleted successfully"
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Failed to delete document"
    ]);
}
?>
