/**
 * @providesModule DataWedgeIntents
 */

'use strict';

var { Platform, NativeModules } = require('react-native');

if (Platform.OS === 'android') {
  var RNDataWedgeIntents = NativeModules.DataWedgeIntents;

  var DataWedgeIntents = {
    sendBroadcastWithExtras(extrasObject) {
      RNDataWedgeIntents.sendBroadcastWithExtras(extrasObject);
    },
    registerBroadcastReceiver(filter) {
      RNDataWedgeIntents.registerBroadcastReceiver(filter);
    },
  };

  module.exports = DataWedgeIntents;
}
