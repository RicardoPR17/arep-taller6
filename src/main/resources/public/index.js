function login() {
  let nameVar = document.getElementById("msg").value;
  const xhttp = new XMLHttpRequest();
  xhttp.onload = function () {
    document.getElementById("jsonmsg").innerHTML = this.responseText;
  };
  xhttp.open("GET", "/login/" + nameVar);
  xhttp.send();
}
