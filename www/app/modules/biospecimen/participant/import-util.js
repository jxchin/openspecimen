angular.module('os.biospecimen.participant')
  .factory('ImportUtil', function($translate, $injector) {
    var pluginTypes = {};

    function addPluginTypes(importTypes, group, entityType) {
      var types = pluginTypes[entityType];
      if (!types || types.length == 0) {
        return;
      }

      angular.forEach(types,
        function(type) {
          type.group = group;
          importTypes.push(type);
        }
      );

      return importTypes;
    }

    function addForms(importTypes, group, entityType, forms) {
      angular.forEach(forms, function(form) {
        if (form.sysForm) {
          return;
        }

        importTypes.push({
          group: group,
          type: 'extensions',
          title: form.caption,
          params: {
            entityType: entityType,
            formName: form.name
          }
        });
      });

      return importTypes;
    }

    function getParticipantTypes(entityForms, cpId, addConsent) {
      var group = $translate.instant('participant.title');

      var importTypes = [];
      if (cpId == -1) {
        importTypes = [
          {
            group: group, type: 'cprMultiple', title: 'participant.registrations',
            showImportType: true, importType: 'CREATE'
          }
        ]
      } else {
        importTypes = [
          {
            group: group, type: 'cpr', title: 'participant.list'
          }
        ]
      }

      if (addConsent) {
        importTypes = importTypes.concat(getConsentTypes(cpId));
      }

      addPluginTypes(importTypes, group, 'Participant');
      addForms(importTypes, group, 'CommonParticipant', entityForms['CommonParticipant']);
      return addForms(importTypes, group, 'Participant', entityForms['Participant']);
    } 

    function getConsentTypes(cpId) {
      var group = $translate.instant('participant.title');
      if ($injector.has('ecDocument')) {
        return [{
          group: group, type: 'econsentsDocumentResponse', title: 'participant.consents',
          showImportType: false, importType: 'UPDATE'
        }];
      } else {
        return [{
          group: group, type: 'consent', title: 'participant.consents',
          showImportType: false, csvType: 'MULTIPLE_ROWS_PER_OBJ', importType: 'UPDATE'
        }];
      }
    }

    function getVisitTypes(entityForms) {
      var group = $translate.instant('visits.title');
      var importTypes = [{ group: group, type: 'visit', title: 'visits.list' }];
      addPluginTypes(importTypes, group, 'SpecimenCollectionGroup');
      return addForms(importTypes, group, 'SpecimenCollectionGroup', entityForms['SpecimenCollectionGroup']);
    }

    function getSpecimenTypes(cp, allowedEntityTypes, entityForms) {
      var group = $translate.instant('specimens.title');

      var importTypes = [];

      importTypes.push({ group: group, type: 'specimen', title: 'specimens.list' });

      if (allowedEntityTypes.indexOf('DerivativeAndAliquots') != -1) {
        importTypes.push({
          group: group, type: 'specimenAliquot', title: 'specimens.spmn_aliquots',
          showImportType: false, importType    : 'CREATE'
        });
        importTypes.push({
          group: group, type: 'specimenDerivative', title: 'specimens.spmn_derivatives',
          showImportType: false, importType    : 'CREATE'
        });
      }

      if (!cp.specimenCentric) {
        importTypes.push({
          group: group, type: 'masterSpecimen', title: 'participant.master_specimens',
          showImportType: false, importType    : 'CREATE'
        });
      }

      importTypes.push({
        group: group, type: 'specimenDisposal', title: 'participant.specimen_disposal',
        showImportType: false, importType    : 'UPDATE'
      });

      addPluginTypes(importTypes, group, 'Specimen');
      addForms(importTypes, group, 'Specimen', entityForms['Specimen']);
      return addForms(importTypes, group, 'SpecimenEvent', entityForms['SpecimenEvent']);
    }

    function getImportDetail(cp, allowedEntityTypes, forms) {
      var breadcrumbs, onSuccess;
      if (cp.id == -1) {
        breadcrumbs = [{state: 'cp-list', title: "cp.list"}];
        onSuccess = {state: 'import-multi-cp-jobs'};
      } else {
        breadcrumbs = [{state: 'cp-list-view', title: cp.shortTitle, params: '{cpId:' + cp.id + '}'}];
        onSuccess = {state: 'import-cp-jobs', params: {cpId: cp.id}};
      }

      var entityForms = {};
      angular.forEach(forms, function(form) {
        if (!entityForms[form.entityType]) {
          entityForms[form.entityType] = [];
        }

        entityForms[form.entityType].push(form);
      });

      var importTypes = [];
      if (!cp.specimenCentric && allowedEntityTypes.indexOf('Participant') >= 0) {
        importTypes = importTypes.concat(getParticipantTypes(entityForms, cp.id, allowedEntityTypes.indexOf('Consent') >= 0));
      } else if (!cp.specimenCentric && allowedEntityTypes.indexOf('Consent') >= 0) {
        importTypes = importTypes.concat(getConsentTypes(cp.id));
      }

      if (!cp.specimenCentric && allowedEntityTypes.indexOf('SpecimenCollectionGroup') >= 0) {
        importTypes = importTypes.concat(getVisitTypes(entityForms));
      }

      if (allowedEntityTypes.indexOf('Specimen') >= 0) {
        importTypes = importTypes.concat(getSpecimenTypes(cp, allowedEntityTypes, entityForms));
      }

      angular.forEach(importTypes,
        function(importType) {
          if (!importType.params) {
            importType.params = {};
          }

          importType.params.cpId = cp.id;
        }
      );

      return {
        breadcrumbs: breadcrumbs,
        title: 'Bulk Import Records',
        objectType: undefined,
        onSuccess: onSuccess,
        types: importTypes,
        objectParams: {
          cpId: cp.id
        }
      };
    }

    return {
      getImportDetail: function(cp, allowedEntityTypes, forms) {
        return $translate('common.none').then(
          function() {
            return getImportDetail(cp, allowedEntityTypes, forms);
          }
        );
      },

      registerTypes: function(entityType, types) {
        if (!types) {
          delete pluginTypes[entityType];
        } else {
          pluginTypes[entityType] = types;
        }
      },

      getPluginTypes: function() {
        var result = [];
        angular.forEach(pluginTypes,
          function(types, key) {
            angular.forEach(types,
              function(type) {
                result.push(type.type);
              }
            );
          }
        );

        return result;
      }
    }
  });
