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

$sql = "
(
    SELECT 
        ss.id,
        ss.scheme_name,
        ss.eligibility_criteria,
        ss.specifications,
        'student' AS scheme_type
    FROM student_schemes ss
    JOIN scheme_documents sd 
        ON ss.id = sd.scheme_id AND sd.scheme_type = 'student'
    WHERE sd.document_type IN (
        SELECT document_type
        FROM user_documents
        WHERE user_id = ?
    )
)
UNION
(
    SELECT 
        fs.id,
        fs.scheme_name,
        fs.eligibility_criteria,
        fs.specifications,
        'farmer' AS scheme_type
    FROM farmer_schemes fs
    JOIN scheme_documents sd 
        ON fs.id = sd.scheme_id AND sd.scheme_type = 'farmer'
    WHERE sd.document_type IN (
        SELECT document_type
        FROM user_documents
        WHERE user_id = ?
    )
)
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("ii", $user_id, $user_id);
$stmt->execute();
$result = $stmt->get_result();

$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode([
    "status" => "success",
    "count" => count($data),
    "data" => $data
]);
