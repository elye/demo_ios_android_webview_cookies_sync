<?php
$key_api="api-cookie-set-key";
$key_webview="webview-cookie-set-key";
$key_native="native-cookie-set-key";

if(!isset($_COOKIE[$key_api])) {
    echo "Cookie named '" . $key_api . "' is not set!<br><br>";
} else {
    $original_value = $_COOKIE[$key_api];
    echo "Cookie from App was: <b>" . $original_value . "</b><br><br>";
}

if(!isset($_COOKIE[$key_webview])) {
    echo "Cookie named '" . $key_webview . "' is not set!<br><br>";
} else {
    $original_value = $_COOKIE[$key_webview];
    echo "Cookie from App was: <b>" . $original_value . "</b><br><br>";
}

if(!isset($_COOKIE[$key_native])) {
    echo "Cookie named '" . $key_native . "' is not set!<br><br>";
} else {
    $original_value = $_COOKIE[$key_native];
    echo "Cookie from App was: <b>" . $original_value . "</b><br><br>";
}

?>
