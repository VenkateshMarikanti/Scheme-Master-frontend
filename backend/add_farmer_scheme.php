<?php
header("Content-Type: application/json");
include "db.php";

if (
    isset($_POST['scheme_name']) &&
    isset($_POST['aadhar_name']) &&
    isset($_POST['land_type']) &&
    isset($_POST['eligibility_criteria']) &&
    isset($_POST['specifications'])
) {

    $scheme_name = $_POST['scheme_name'];
    $aadhar_name = $_POST['aadhar_name'];
    $land_type = $_POST['land_type'];
    $eligibility_criteria = $_POST['eligibility_criteria'];
    $specifications = $_POST['specifications'];

    $stmt = mysqli_prepare(
        $conn,
        "INSERT INTO farmer_schemes
        (scheme_name, aadhar_name, land_type, eligibility_criteria, specifications)
        VALUES (?, ?, ?, ?, ?)"
    );

    mysqli_stmt_bind_param(
        $stmt,
        "sssss",
        $scheme_name,
        $aadhar_name,
        $land_type,
        $eligibility_criteria,
        $specifications
    );

    mysqli_stmt_execute($stmt);

    echo json_encode([
        "status" => "success",
        "message" => "Farmer scheme added successfully"
    ]);

} else {
    echo json_encode([
        "status" => "error",
        "message" => "Required fields missing"
    ]);
}
?>
