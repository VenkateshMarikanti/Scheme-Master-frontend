<?php
header("Content-Type: application/json");
include "db.php";

$user_id = $_POST['user_id'] ?? '';
$document_type = $_POST['document_type'] ?? '';

if (!$user_id || !$document_type || !isset($_FILES['document'])) {
    echo json_encode([
        "status" => "error",
        "message" => "Missing required fields"
    ]);
    exit;
}

$file = $_FILES['document'];
$allowed_types = ['jpg', 'jpeg', 'png', 'pdf'];

$ext = strtolower(pathinfo($file['name'], PATHINFO_EXTENSION));

if (!in_array($ext, $allowed_types)) {
    echo json_encode([
        "status" => "error",
        "message" => "Only JPG, PNG, PDF allowed"
    ]);
    exit;
}

$upload_dir = "uploads/";
if (!is_dir($upload_dir)) {
    mkdir($upload_dir, 0777, true);
}

$new_file_name = time() . "_" . uniqid() . "." . $ext;
$file_path = $upload_dir . $new_file_name;

if (move_uploaded_file($file['tmp_name'], $file_path)) {

    $stmt = mysqli_prepare(
        $conn,
        "INSERT INTO user_documents (user_id, document_type, file_name, file_path)
         VALUES (?, ?, ?, ?)"
    );

    mysqli_stmt_bind_param(
        $stmt,
        "isss",
        $user_id,
        $document_type,
        $new_file_name,
        $file_path
    );

    if (mysqli_stmt_execute($stmt)) {
        echo json_encode([
            "status" => "success",
            "message" => "Document uploaded successfully"
        ]);
    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Database insert failed"
        ]);
    }

} else {
    echo json_encode([
        "status" => "error",
        "message" => "File upload failed"
    ]);
}
?>
