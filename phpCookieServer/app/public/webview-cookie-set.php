<?php
$key="webview-cookie-set-key";
$value="this-is-set-by-webview";
if(!isset($_COOKIE[$key])) {
    setcookie($key, $value, time()+3600);
    echo "Cookie named '" . $key . "' is now set to <b>" . $value . "</b>!";
} else {
    $original_value = $_COOKIE[$key];
    echo "Cookie with name: <b>" . $key . "</b> already set to " . $original_value . "<br>";
    echo "Do nothing here";
}
?>
