 <?php
$servername = "localhost";
$username = "root";
$password = "";
$conn = null;
// Create connection
if(isset($_POST["db"])){
	$conn = new mysqli($servername, $username, $password, $_POST["db"]);
}

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
else {
	if (!$conn->query($_POST["query"])) {
        	echo $conn->error;
    }
}

?> 