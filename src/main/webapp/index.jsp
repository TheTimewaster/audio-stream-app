<html>
	<body>
		<h2>Hello World!</h2>
		<button onclick="streamSong()">
		click me!
		</button>

		<script type="text/javascript">
			window.AudioContext = window.AudioContext||window.webkitAudioContext;
			context = new AudioContext()

			function streamSong()
			{
				var request = new XMLHttpRequest();
				request.open("GET","/stream_now", true);
				request.responseType = "arraybuffer"; 

				console.log("anything happens?")

				request.onload = function() {
					console.log("request.onload")
					var Data = request.response;
					process(Data);
				};

				request.send();
			}

			function process(Data)
			{
				console.log("processData")

				source = context.createBufferSource();
				context.decodeAudioData(Data, function(buffer)
				{
					source.buffer = buffer;
					source.connect(context.destination);
					source.start(context.currentTime);
				})
			}
		</script>
	</body>
</html>
