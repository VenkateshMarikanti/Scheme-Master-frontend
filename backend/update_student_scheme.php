<?php
header("Content-Type: application/json");
include "db.php";

if (
    isset($_POST['id']) &&
    isset($_POST['scheme_name']) &&
    isset($_POST['aadhar_name']) &&
    isset($_POST['caste_name']) &&
    isset($_POST['eligibility_criteria']) &&
    isset($_POST['specifications'])
) {

    $id = $_POST['id'];
    $scheme_name = $_POST['scheme_name'];
    $aadhar_name = $_POST['aadhar_name'];
    $caste_name = $_POST['caste_name'];
    $eligibility_criteria = $_POST['eligibility_criteria'];
    $specifications = $_POST['specifications'];

    $stmt = mysqli_prepare(
        $conn,
        "UPDATE student_schemes 
         SET scheme_name=?, aadhar_name=?, caste_name=?, eligibility_criteria=?, specifications=?
         WHERE id=?"
    );

    mysqli_stmt_bind_param(
        $stmt,
        "sssssi",
        $scheme_name,
        $aadhar_name,
        $caste_name,
        $eligibility_criteria,
        $specifications,
        $id
    );

    if (mysqli_stmt_execute($stmt)) {
        echo json_encode([
            "status" => "success",
            "message" => "Student scheme updated successfully"
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
        "message" => "Required fields missing"
    ]);
}
?>
