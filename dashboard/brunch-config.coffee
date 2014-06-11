exports.config =
	paths:
		public: 'public'
		watched: ['src', 'test', 'haven_artifacts']
	conventions:
		assets: /(assets|build)(\/|\\)/
	files:
		javascripts:
			joinTo:
				'js/jade-runtime.js': /.+jade-runtime.js$/
				'js/app.js': /^src/
				'js/libraries.js': /^haven_artifacts/
			order:
				before:[
					'haven_artifacts/main/jquery/jquery.js'
				]
		stylesheets:
			joinTo:
				'css/app.css': /^(src)/
				'css/libraries.css': /^(haven_artifacts)/
		templates:
			joinTo: 
				'js/templates.js' : /.+\.jade$/
	modules:
		wrapper: false
		definition: false
	plugins:
		jade:
			pretty: yes
		static_jade:               
			asset: "src/build"
