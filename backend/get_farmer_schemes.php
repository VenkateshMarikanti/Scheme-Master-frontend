<?php
header("Content-Type: application/json");
include "db.php";

$result = mysqli_query($conn, "SELECT * FROM farmer_schemes");
$data = [];

while ($row = mysqli_fetch_assoc($result)) {
    $data[] = $row;
}

echo json_encode([
    "status" => "success",
    "data" => $data
]);
?>
