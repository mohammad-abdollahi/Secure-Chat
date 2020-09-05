
<form  name = ""  action = ""  method = "GET" > 
<input  type = "text"  name = "content" / > 
<input  type = "Submit"  value = "save" / > 
</form >

<?php 

if(isset($_GET["content"]))
{
	echo "result: ".$_GET["content"];
}
?>

