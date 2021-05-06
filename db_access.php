 <?php
$servername = "localhost:3308";
$username = "jeffrey2";
$password = "mysqlUsernamePassword";

// Create connection
$conn = new mysqli($servername, $username, $password);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
echo "Connected successfully";
?> 