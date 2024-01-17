<h1>Chat App</h1>
<h2>Descripción del proyecto</h2>
<p>Aplicación de mensajería en tiempo real. Puedes crear una cuenta y empezar a mensajear con los usuarios registrados.</p>
<h2>Funcionalidades</h2>
<p>&#9989 Crear cuenta</p>
<p>&#9989 Mensajear con otros usuarios en tiempo real</p>
<p>Descarga el apk <a href="https://github.com/ivancraco/chatapp/blob/master/chat_app.apk" download="chat_app">aquí</a>
</p>
<button onclick="downloadAPK()">Descargar APK</button>

<h2>Autor</h2>
<span>Iván Craco</span>

<script>
  function downloadAPK() {
  var url = "[https://github.com/](https://github.com/)[Tu nombre de usuario]/[Nombre del repositorio]/blob/main/[Nombre del APK]";
  var filename = "mi-aplicacion.apk";

  var xhr = new XMLHttpRequest();
  xhr.open("GET", url, true);
  xhr.responseType = "arraybuffer";
  xhr.onload = function() {
    var arrayBuffer = xhr.response;
    var blob = new Blob([arrayBuffer], {type: "application/vnd.android.package-archive"});
    var url = URL.createObjectURL(blob);
    var download = document.createElement("a");
    download.href = url;
    download.download = filename;
    document.body.appendChild(download);
    download.click();
  };
  xhr.send();
}
</script>
