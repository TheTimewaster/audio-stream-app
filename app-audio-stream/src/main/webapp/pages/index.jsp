<html>
	<body>
		<h2>Hello World!</h2>
		<button onclick="streamSong();">
		click me!
		</button>

		<script type="text/javascript">
			window.AudioContext = window.AudioContext||window.webkitAudioContext;
			context = new AudioContext()

			function streamSong()
			{
				var request = new XMLHTTPServlet();+
				request.open("GET","http:localhost:8080/stream_now");

				request.onload = function()
				{
					var Data = request.response;
					process(Data);
				}

				request.send;
			}

			function processData(Data)
			{
				source = context.createBufferSource();
				context.decodeAudioData(Data, function(buffer))
				{
					source.buffer = buffer;
					source.connect(context.destination);
					source.start(context.currentTime);
				}
			}
		</script>
	</body>
</html>
