<?php
header("Content-Type: application/json");
include "db.php";

if (isset($_POST['id'])) {

    $stmt = mysqli_prepare(
        $conn,
        "DELETE FROM farmer_schemes WHERE id=?"
    );
    mysqli_stmt_bind_param($stmt, "i", $_POST['id']);
    mysqli_stmt_execute($stmt);

    echo json_encode([
        "status" => "success",
        "message" => "Farmer scheme deleted successfully"
    ]);

} else {
    echo json_encode([
        "status" => "error",
        "message" => "ID required"
    ]);
}
?>
