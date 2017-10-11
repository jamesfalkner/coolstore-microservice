'use strict';

angular.module("app")

    .factory('Inventory', ['$http', '$q', 'COOLSTORE_CONFIG', 'Auth', '$location',
        function ($http, $q, COOLSTORE_CONFIG, $auth, $location) {
        var factory = {}, config = undefined, baseUrl;
        if ($location.protocol() === 'https') {
            baseUrl = (COOLSTORE_CONFIG.SECURE_API_ENDPOINT.startsWith("https://") ? COOLSTORE_CONFIG.SECURE_API_ENDPOINT : "https://" + COOLSTORE_CONFIG.SECURE_API_ENDPOINT + '.' + $location.host().replace(/^.*?\.(.*)/g, "$1")) + '/api/availability';
        } else {
            baseUrl = (COOLSTORE_CONFIG.API_ENDPOINT.startsWith("http://") ? COOLSTORE_CONFIG.API_ENDPOINT : "http://" + COOLSTORE_CONFIG.API_ENDPOINT + '.' + $location.host().replace(/^.*?\.(.*)/g, "$1")) + '/api/availability';
        }

        factory.saveConfig = function (newConfig) {
            var deferred = $q.defer();
            $http({
                method: 'POST',
                url: baseUrl + "/config",
			    data: newConfig
            }).then(function () {
                config = newConfig;
                deferred.resolve();
            }, function (err) {
                deferred.reject(err);
            });
            return deferred.promise;
        };

        factory.getConfig = function() {
            var deferred = $q.defer();

            if (config) {
                deferred.resolve(config);
            } else {
                $http({
                    method: 'GET',
                    url: baseUrl + "/config"
                }).then(function(resp) {
                    config = resp.data;
                    deferred.resolve(config);
                }, function(err) {
                    deferred.reject(err);
                });
            }
            return deferred.promise;
        };

        factory.getConfig();

        return factory;
    }]);
