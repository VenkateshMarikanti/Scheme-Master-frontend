<?php
header("Content-Type: application/json");
include "db.php";

if (isset($_POST['id'])) {

    $id = $_POST['id'];

    $stmt = mysqli_prepare(
        $conn,
        "DELETE FROM student_schemes WHERE id=?"
    );
    mysqli_stmt_bind_param($stmt, "i", $id);

    if (mysqli_stmt_execute($stmt)) {
        echo json_encode([
            "status" => "success",
            "message" => "Student scheme deleted successfully"
        ]);
    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Delete failed"
        ]);
    }

} else {
    echo json_encode([
        "status" => "error",
        "message" => "ID required"
    ]);
}
?>
