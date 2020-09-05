<?php

$servername = "localhost";
$username = "mohammad";
$password = "m13770606";
$dbname = "StoredXss";

$conn = new mysqli($servername, $username, $password, $dbname);

if (isset($_POST['message']))
{
	$sql = "INSERT INTO messages (message)
	VALUES ('".addslashes($_POST['message'])."')";
}

?>
<!DOCTYPE html>
<html>
<title>Xss</title>
<style>
	#bord td
	{
		border: 1px solid black;
		border-collapse: collapse;
	}
</style>
<body>
<table>
<tr><td>
<p>
<?php
if (isset($_POST['message']))
{
	if ($conn->query($sql) === TRUE) 
	{
		echo "Added successfully";
	} else {
		echo "Error :(((";
	}
}
?>
</p>
<form action="temp.php" method="post">
	<textarea rows="7" cols="40" name="message" placeholder="Add your message"></textarea>
	<table align="center"><tr><td>
	<input type="submit" value="Send" />
	</td></tr></table>
</form>
</td></tr>
</table>
<br />
<br />
<table id="bord">
<?php
$sql = "SELECT id, message FROM messages";
$result = $conn->query($sql);

if ($result->num_rows) {
    while($row = $result->fetch_assoc()) {
        echo "<tr><td>message #".$row["id"]."<br /><hr />".$row["message"]."<br /></td></tr>";
    }
} 
$conn->close();
?>
</table>

</body>
</html> 
