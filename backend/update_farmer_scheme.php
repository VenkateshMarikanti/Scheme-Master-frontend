<?php
header("Content-Type: application/json");
include "db.php";

if (
    isset($_POST['id']) &&
    isset($_POST['scheme_name']) &&
    isset($_POST['aadhar_name']) &&
    isset($_POST['land_type']) &&
    isset($_POST['eligibility_criteria']) &&
    isset($_POST['specifications'])
) {

    $stmt = mysqli_prepare(
        $conn,
        "UPDATE farmer_schemes 
         SET scheme_name=?, aadhar_name=?, land_type=?, eligibility_criteria=?, specifications=?
         WHERE id=?"
    );

    mysqli_stmt_bind_param(
        $stmt,
        "sssssi",
        $_POST['scheme_name'],
        $_POST['aadhar_name'],
        $_POST['land_type'],
        $_POST['eligibility_criteria'],
        $_POST['specifications'],
        $_POST['id']
    );

    mysqli_stmt_execute($stmt);

    echo json_encode([
        "status" => "success",
        "message" => "Farmer scheme updated successfully"
    ]);

} else {
    echo json_encode([
        "status" => "error",
        "message" => "Required fields missing"
    ]);
}
?>
