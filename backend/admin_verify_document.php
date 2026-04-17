<?php
header("Content-Type: application/json");
include "db.php";

if (
    isset($_POST['document_id']) &&
    isset($_POST['status'])
) {
    $document_id = $_POST['document_id'];
    $status = $_POST['status']; // approved or rejected

    if (!in_array($status, ['approved', 'rejected'])) {
        echo json_encode([
            "status" => "error",
            "message" => "Invalid status"
        ]);
        exit;
    }

    $stmt = mysqli_prepare(
        $conn,
        "UPDATE documents SET status=? WHERE document_id=?"
    );
    mysqli_stmt_bind_param($stmt, "si", $status, $document_id);

    if (mysqli_stmt_execute($stmt)) {
        echo json_encode([
            "status" => "success",
            "message" => "Document status updated successfully"
        ]);
    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Update failed"
        ]);
    }

} else {
    echo json_encode([
        "status" => "error",
        "message" => "Document ID and status required"
    ]);
}
?>
