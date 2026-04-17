<?php
header("Content-Type: application/json");
include "db.php";

$query = "
SELECT 
    d.document_id,
    d.user_id,
    d.document_type,
    d.file_name,
    d.file_path,
    d.status,
    d.uploaded_at
FROM documents d
ORDER BY d.uploaded_at DESC
";

$result = mysqli_query($conn, $query);

$data = [];

while ($row = mysqli_fetch_assoc($result)) {
    $data[] = $row;
}

echo json_encode([
    "status" => "success",
    "data" => $data
]);
?>
