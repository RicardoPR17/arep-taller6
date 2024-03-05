function login() {
  let msgVar = document.getElementById("msg").value;
  const xhttp = new XMLHttpRequest();
  xhttp.onload = function () {
    document.getElementById("jsonmsg").innerHTML = this.responseText;
  };
  xhttp.open("GET", "/logservicefacade?msg=" + msgVar);
  xhttp.send();
}
