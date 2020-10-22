angular.module('os.biospecimen.models.cpr', 
  [
    'os.common.models', 
    'os.biospecimen.models.participant',
    'os.biospecimen.models.visit',
    'os.biospecimen.models.form'
  ])
  .factory('CollectionProtocolRegistration', function(
    $filter, $http, $parse, $injector, osModel,
    Participant, Visit, Form, Util, ExtensionsUtil) {

    var CollectionProtocolRegistration = 
      osModel(
        'collection-protocol-registrations',
        function(cpr) {
          cpr.participant = new Participant(cpr.participant);
        }
      );
 
    //
    // overriding default query function
    //
    CollectionProtocolRegistration.query = function(queryParams) {
      return $http.post(CollectionProtocolRegistration.url() + '/list', queryParams)
        .then(CollectionProtocolRegistration.modelArrayRespTransform);
    }

    //
    // overriding default getCount function
    //
    CollectionProtocolRegistration.getCount = function(queryParams) {
      return $http.post(CollectionProtocolRegistration.url() + 'count', queryParams)
        .then(CollectionProtocolRegistration.noTransform);
    }

    CollectionProtocolRegistration.listForCp = function(cpId, includeStats, filterOpts) {
      return CollectionProtocolRegistration.query(prepareFilterOpts(cpId, includeStats, filterOpts));
    }

    CollectionProtocolRegistration.getCprCount = function(cpId, includeStats, filterOpts) {
      return CollectionProtocolRegistration.getCount(prepareFilterOpts(cpId, includeStats, filterOpts));
    }

    CollectionProtocolRegistration.bulkRegistration = function(bulkRegDetail) {
      var url = CollectionProtocolRegistration.url() + '/bulk';
      return $http.post(url, bulkRegDetail).then(CollectionProtocolRegistration.modelRespTransform);
    }

    //Update registrations in bulk with same detail
    CollectionProtocolRegistration.bulkEdit = function(detail) {
      return $http.put(CollectionProtocolRegistration.url() + "bulk-update", detail)
        .then(CollectionProtocolRegistration.modelArrayRespTransform);
    }

    CollectionProtocolRegistration.bulkDelete = function(cprIds, reason) {
      return $http.delete(CollectionProtocolRegistration.url(), {params: {id: cprIds, forceDelete: true, reason: reason}})
        .then(function(result) { return result.data; });
    }

    function prepareFilterOpts(cpId, includeStats, filterOpts) {
      var params = {cpId: cpId, includeStats: !!includeStats};
      angular.extend(params, filterOpts || {});

      //
      // Note: yyyy-MM-dd is server date format and is not locale based
      //
      if (!!params.dob) {
        params.dob = $filter('date')(params.dob, 'yyyy-MM-dd');
      }

      if (!!params.registrationDate) {
        params.registrationDate = $filter('date')(params.registrationDate, 'yyyy-MM-dd');
      }

      return params;
    }

    CollectionProtocolRegistration.prototype.getType = function() {
      return 'collection_protocol_registration';
    }

    CollectionProtocolRegistration.prototype.getDisplayName = function() {
      var str = this.ppid;
      if (!!this.participant.firstName || !!this.participant.lastName) {
        str += " (" + this.participant.firstName + " " + this.participant.lastName + ")";
      }
      return str;
    };

    CollectionProtocolRegistration.prototype.getMrnSites = function() {
      return !this.participant ? undefined : this.participant.getMrnSites();
    };

    CollectionProtocolRegistration.prototype.getForms = function(params) {
      return Form.listFor(CollectionProtocolRegistration.url(), this.$id(), params);
    };

    CollectionProtocolRegistration.prototype.getRecords = function() {
      var url = CollectionProtocolRegistration.url() + this.$id() + '/extension-records';
      return Form.listRecords(url);
    };

    CollectionProtocolRegistration.prototype.$saveProps = function() {
      this.participant = this.participant.$saveProps();
      return this;
    };

    CollectionProtocolRegistration.prototype.getSignedConsentFormUrl = function() {
      return CollectionProtocolRegistration.url() + this.$id() + "/consent-form";
    }

    CollectionProtocolRegistration.prototype.deleteSignedConsentForm = function() {
      return $http.delete(this.getSignedConsentFormUrl()).then(function(result){ return result.data; });
    }

    CollectionProtocolRegistration.prototype.saveConsentResponse = function(consents) {
      var url = CollectionProtocolRegistration.url() + this.$id() + "/consents";
      return $http.put(url, consents).then(function(result) {return result.data;});
    }

    CollectionProtocolRegistration.prototype.getConsents = function() {
      if ($injector.has('ecDocResponse')) {
        return $injector.get('ecDocResponse').getConsents(this.$id());
      }

      var url = CollectionProtocolRegistration.url() + this.$id() + "/consents";
      return $http.get(url).then(function(result) {return result.data;});
    }

    CollectionProtocolRegistration.prototype.getLatestVisit = function() {
      var url = CollectionProtocolRegistration.url() + this.$id() + "/latest-visit";
      return $http.get(url).then(Visit.modelRespTransform);
    }

    CollectionProtocolRegistration.prototype.anonymize = function() {
      var url = CollectionProtocolRegistration.url() + this.$id() + "/anonymize";
      return $http.put(url).then(CollectionProtocolRegistration.modelRespTransform);
    }

    CollectionProtocolRegistration.prototype.getAllowedEvents = function(visitsTab) {
      if (!visitsTab.anticipatedEvents) {
        return null;
      }

      ExtensionsUtil.createExtensionFieldMap(this.participant);
      for (var i = 0; i < visitsTab.anticipatedEvents.length; ++i) {
        var spec = visitsTab.anticipatedEvents[i];
        if ($parse(spec.rule)({cpr: this})) {
          return spec.events;
        }
      }

      return null;
    }

    return CollectionProtocolRegistration;
  });
