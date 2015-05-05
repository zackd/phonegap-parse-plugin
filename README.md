Phonegap Parse.com Plugin
=========================

Phonegap 3.0.0 plugin for Parse.com push service

Using [Parse.com's](http://parse.com) REST API for push requires the installation id, which isn't available in JS

This plugin exposes the four native Android API push services to JS:
* <a href="https://www.parse.com/docs/android/api/com/parse/ParseInstallation.html#getInstallationId()">getInstallationId</a>
* <a href="https://www.parse.com/docs/android/api/com/parse/PushService.html#getSubscriptions(android.content.Context)">getSubscriptions</a>
* <a href="https://www.parse.com/docs/android/api/com/parse/PushService.html#subscribe(android.content.Context, java.lang.String, java.lang.Class, int)">subscribe</a>
* <a href="https://www.parse.com/docs/android/api/com/parse/PushService.html#unsubscribe(android.content.Context, java.lang.String)">unsubscribe</a>

Installation
------------

Pick one of these two commands:

```
phonegap local plugin add https://github.com/benjie/phonegap-parse-plugin
cordova plugin add https://github.com/benjie/phonegap-parse-plugin
```

Initial Setup
-------------

Once the device is ready, call ```parsePlugin.initialize()```. This will register the device with Parse, you should see this reflected in your Parse control panel. After this runs you probably want to save the installationID somewhere, and perhaps subscribe the user to a few channels. Here is a contrived example.

```
parsePlugin.initialize(appId, clientKey, function() {

	parsePlugin.subscribe('SampleChannel', function() {

		parsePlugin.getInstallationId(function(id) {

			/**
			 * Now you can construct an object and save it to your own services, or Parse, and corrilate users to parse installations
			 *
			 var install_data = {
			  	installation_id: id,
			  	channels: ['SampleChannel']
			 }
			 *
			 */

		}, function(e) {
			alert('error');
		});

	}, function(e) {
		alert('error');
	});

}, function(e) {
	alert('error');
});

```

If you want to use message handler on javascript side you have to declare your action in your config.xml,
and your parse push message should include {action: 'com.myapp.myapp.MY_MESSAGE_NAME'}

```
<receiver android:name="org.apache.cordova.core.ParseReceiver">
		<intent-filter>
				<action android:name="com.myapp.myapp.MY_MESSAGE_NAME" />
		</intent-filter>
</receiver>
```


Usage
-----
```
<script type="text/javascript">
	parsePlugin.initialize(appId, clientKey, function() {
		alert('success');
	}, function(e) {
		alert('error');
	});

	parsePlugin.getInstallationId(function(id) {
		alert(id);
	}, function(e) {
		alert('error');
	});

	parsePlugin.getSubscriptions(function(subscriptions) {
		alert(subscriptions);
	}, function(e) {
		alert('error');
	});

	parsePlugin.subscribe('SampleChannel', function() {
		alert('OK');
	}, function(e) {
		alert('error');
	});

	parsePlugin.unsubscribe('SampleChannel', function(msg) {
		alert('OK');
	}, function(e) {
		alert('error');
	});

	parsePlugin.onMessage(function (message) {
		console.log(message.payload.myField); // this is for android
		console.log(message.myField);         // this works for iOS
	}, function (err) {
		alert('error');
	});
</script>
```

Compatibility
-------------
Phonegap > 3.0.0
