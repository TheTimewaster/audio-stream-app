<html>
	<body>
		<h2>Hello World!</h2>
		<button onclick="streamSong();">
		click me!
		</button>

		<script type="text/javascript">
			window.AudioContext = window.AudioContext||window.webkitAudioContext;
			context = new AudioContext();

			function streamSong()
			{
				var request = new XMLHttpRequest();
				request.open("GET","/stream_now", true);
				request.responseType = "arraybuffer"; 

				request.onload = function()
				{
					var Data = request.response;
					processData(Data);
				};

				request.send();
			}

			function processData(Data)
			{
				source = context.createBufferSource();
				context.decodeAudioData(Data, function(buffer)
				{
					console.log("asdf");
					source.buffer = buffer;
					source.connect(context.destination);
					source.start(0);
				});
			}
		</script>
	</body>
</html>
