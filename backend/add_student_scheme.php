<?php
header("Content-Type: application/json");
include "db.php";

if (
    isset($_POST['scheme_name']) &&
    isset($_POST['aadhar_name']) &&
    isset($_POST['caste_name']) &&
    isset($_POST['eligibility_criteria']) &&
    isset($_POST['specifications'])
) {

    $scheme_name = $_POST['scheme_name'];
    $aadhar_name = $_POST['aadhar_name'];
    $caste_name = $_POST['caste_name'];
    $eligibility_criteria = $_POST['eligibility_criteria'];
    $specifications = $_POST['specifications'];

    $stmt = mysqli_prepare(
        $conn,
        "INSERT INTO student_schemes
        (scheme_name, aadhar_name, caste_name, eligibility_criteria, specifications)
        VALUES (?, ?, ?, ?, ?)"
    );

    mysqli_stmt_bind_param(
        $stmt,
        "sssss",
        $scheme_name,
        $aadhar_name,
        $caste_name,
        $eligibility_criteria,
        $specifications
    );

    if (mysqli_stmt_execute($stmt)) {
        echo json_encode([
            "status" => "success",
            "message" => "Student scheme added successfully"
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
        "message" => "Required fields missing"
    ]);
}

?>
