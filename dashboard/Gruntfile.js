module.exports = function(grunt) {

  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    clean: ["dist"],
    jade: {
      html: {
        src: ['src/*.jade'],
        dest: 'dist/',
        options: {
          client: false
        }
      }
    },
    concat: {
      dist: {
        files: {
          'dist/js/libraries.js': ['haven_artifacts/main/**/*.js'],
          'dist/js/app.js': ['src/js/**/*.js'],
          'dist/css/libraries.css': ['haven_artifacts/main/**/*.css'],
          'dist/css/app.css': ['src/css/**/*.css']
        }
      },
    },
    copy: {
      dist: {
        files: {
          'dist/js/config.js': ['src/config/config.js']
        }
      }
    },
    compress: {
      main: {
        options: {
          mode: "tgz",
          archive: 'dist/cloud-dashboard.tar.gz'
        },
        files: [{
            expand: true,
            src: ['**'],
            cwd: "dist",
            dest: '.',
          }
        ]
      }
    }
  });

  // Plugins
  grunt.loadNpmTasks('grunt-jade');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-contrib-compress');
  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-haven');

  // Tasks
  grunt.registerTask('update', ['haven:update']);
  grunt.registerTask('build', ['clean', 'jade', 'concat', 'copy', 'compress']);
  grunt.registerTask('deploy', ['update', 'build', 'haven:deploy']);

  // Default task(s).
  grunt.registerTask('default', ['build']);

};