
angular.module('openspecimen')
  .factory('CpConfigSvc', function(CollectionProtocol, $q) {
    var cpWorkflowsMap = {};

    var cpWorkflowsQ = {};

    var listCfgsMap = {};

    var summarySt = undefined;
    
    function getRegParticipantTmpl(cpId, cprId) {
      return getTmpl(cpId, cprId, 'registerParticipant', 'modules/biospecimen/participant/addedit.html');
    }

    function getRegParticipantCtrl(cpId, cprId) {
      return getCtrl(cpId, cprId, 'registerParticipant','ParticipantAddEditCtrl');
    }

    function getBulkRegParticipantTmpl(cpId, cprId) {
      return getTmpl(cpId, cprId, 'registerBulkParticipant');
    }

    function getBulkRegParticipantCtrl(cpId, cprId) {
      return getCtrl(cpId, cprId, 'registerBulkParticipant');
    }

    function getTmpl(cpId, cprId, name, defaultTmpl){
      var cfg = cpWorkflowsMap[cpId];
      var workflow = cfg.workflows[name];
      if (workflow) {
        return workflow.view;
      }
      return defaultTmpl;      
    }

    function getCtrl(cpId, cprId, name, defaultCtrl){
      var cfg = cpWorkflowsMap[cpId];
      var workflow = cfg.workflows[name];
      if (workflow) {
        return workflow.ctrl;
      }
      return defaultCtrl;      
    }

    function loadWorkflows(cpId) {
      var d = $q.defer();
      if (cpWorkflowsMap[cpId]) {
        d.resolve(cpWorkflowsMap[cpId]);
        return d.promise;
      }

      if (cpWorkflowsQ[cpId]) {
        return cpWorkflowsQ[cpId];
      }

      cpWorkflowsQ[cpId] = CollectionProtocol.getWorkflows(cpId).then(
        function(cpWorkflows) {
          cpWorkflowsMap[cpId] = cpWorkflows;
          delete cpWorkflowsQ[cpId];
          return cpWorkflows;
        },

        function() {
          delete cpWorkflowsQ[cpId];
        }
      );

      return cpWorkflowsQ[cpId];
    }

    function getWorkflowData(cpId, name, defVal) {
      return loadWorkflows(cpId).then(
        function(cfg) {
          var workflow = cfg.workflows[name];
          return workflow ? (workflow.data || defVal || {}) : (defVal || {});
        }
      );
    }

    function getWorkflow(cpId, name) {
      return loadWorkflows(cpId).then(
        function(cfg) {
          return cfg.workflows[name];
        }
      );
    }

    function saveWorkflow(cpId, workflow) {
      return CollectionProtocol.saveWorkflows(cpId, [workflow], true).then(
        function(resp) {
          if (cpWorkflowsMap[cpId]) {
            cpWorkflowsMap[cpId].workflows[workflow.name] = workflow;
          }

          return resp;
        }
      );
    }

    function getCommonCfg(cpId, propName) {
      return getConfig(cpId, 'common', propName);
    }

    function getConfig(cpId, wfName, propName) {
      cpId = cpId || -1;
      return getWorkflowData(cpId, wfName).then(
        function(data) {
          if ((data[propName] != null && data[propName] != undefined) || cpId == -1) {
            return data[propName];
          }

          return getWorkflowData(-1, wfName).then(
            function(data) {
              return data[propName];
            }
          );
        }
      );
    }

    function getValue(cpId, wfName, propName) {
      var result = {status: 'ok', value: undefined};
      getConfig(cpId, wfName, propName).then(
        function(cfg) {
          result.value = cfg;
        }
      );

      return result;
    }

    function getOnValueChangeFns(cpId, wfOrder, result) {
      if (wfOrder.length == 0) {
        return result;
      }

      return getWorkflowData(cpId, wfOrder[0], {}).then(
        function(data) {
          result = angular.extend(result, data.onValueChange || {});
          return getOnValueChangeFns(cpId, wfOrder.slice(1), result);
        }
      );
    }

    function setWorkflows(cp, workflows) {
      cpWorkflowsMap[cp.id] = workflows;
      delete cpWorkflowsQ[cp.id];

      var pattern = 'cp-' + cp.id + '-';
      var matchingKeys = [];
      angular.forEach(listCfgsMap,
        function(value, key) {
          if (key.indexOf(pattern) == 0) {
            matchingKeys.push(key);
          }
        }
      );

      angular.forEach(matchingKeys,
        function(key) {
          delete listCfgsMap[key];
        }
      );
    }

    function getBulkUpdateDictionary() {
      return getWorkflowData(-1, 'bulk-update-dictionary', {}).then(
        function(bud) {
          return {cpId: -1, fields: bud.fields || []};
        }
      );
    }

    return {
      getRegParticipantTmpl: function(cpId, cprId) {
        if (cprId != -1) { //edit case
          return 'modules/biospecimen/participant/addedit.html';
        }

        return loadWorkflows(cpId).then(
          function() {
            return getRegParticipantTmpl(cpId, cprId);
          }
        );
      },

      getRegParticipantCtrl : function(cpId, cprId) {
        if (cprId != -1) { // edit case
          return 'ParticipantAddEditCtrl';
        } 

        // we do not call loadWorkflows, as it would have been loaded by above 
        // template provider
        return getRegParticipantCtrl(cpId, cprId);
      },

      getWorkflow: getWorkflow,

      getWorkflowData: getWorkflowData,

      getBulkRegParticipantTmpl: function(cpId, cprId) {
        return loadWorkflows(cpId).then(
          function() {
            return getBulkRegParticipantTmpl(cpId, cprId);
          }
        );
      },

      getBulkRegParticipantCtrl: function(cpId, cprId) {
        // we do not call loadWorkflows, as it would have been loaded by above 
        // template provider
        return getBulkRegParticipantCtrl(cpId, cprId);
      },

      getDictionary: function(cpId, defValue) {
        return getWorkflowData(cpId, 'dictionary').then(
          function(data) {
            if (data.fields) {
              return data.fields;
            }

            return getWorkflowData(-1, 'dictionary').then(
              function(sysDict) {
                return sysDict.fields || defValue || [];
              }
            );
          }
        );
      },

      getBulkUpdateDictionary: function(cpId) {
        return getWorkflowData(cpId, 'dictionary', {}).then(
          function(wfData) {
            if (wfData.fields && wfData.fields.length > 0) {
              return {cpId: cpId, fields: wfData.fields};
            } else if (cpId == -1) {
              return getBulkUpdateDictionary();
            } else {
              return getWorkflowData(-1, 'dictionary', {}).then(
                function(sysWfData) {
                  if (sysWfData.fields && sysWfData.fields.length > 0) {
                    return {cpId: -1, fields: sysWfData.fields};
                  } else {
                    return getBulkUpdateDictionary();
                  }
                }
              );
            }
          }
        );
      },

      getLayout: function(cpId, defValue) {
        return getWorkflowData(cpId, 'dictionary').then(
          function(data) {
            if (data.layout) {
              return data.layout;
            }

            return getWorkflowData(-1, 'dictionary').then(
              function(sysDict) {
                return sysDict.layout || defValue || [];
              }
            );
          }
        );
      },

      getOnValueChangeCallbacks: function(cpId, wfLuOrder) {
        return getWorkflowData(-1, 'dictionary', {}).then(
          function(data) {
            var result = angular.extend({}, data.onValueChange || {});
            return getOnValueChangeFns(cpId, (wfLuOrder || []).slice().reverse(), result);
          }
        );
      },

      setSummaryState: function(summaryState) {
        summarySt = summaryState;
      },

      getSummaryState: function() {
        return summarySt;
      },

      getListView: function(cpId, defValue) {
        return getWorkflowData(cpId, 'common').then(
          function(data) {
            return data.listView || defValue;
          }
        );
      },

      getListConfig: function(cp, listName) {
        var key = 'cp-' + cp.id + '-' + listName;
        if (!listCfgsMap[key]) {
          listCfgsMap[key] = cp.getListConfig(listName);
        }

        return listCfgsMap[key].then(
          function(cfg) {
            return cfg;
          }
        );
      },

      getLockedParticipantFields: function(src) {
        return getWorkflowData(-1, 'locked-fields').then(
          function(data) {
            if (!data) {
              return [];
            }

            return (data.participant || {})[src || 'OpenSpecimen'] || [];
          }
        );
      },

      getSpecimenTreeCfg: function(cpId) {
        return getWorkflowData(-1, 'specimenTree', {}).then(
          function(sysTreeCfg) {
            var result = angular.extend({}, sysTreeCfg);
            if (cpId == -1) {
              return result;
            }

            return getWorkflowData(cpId, 'specimenTree', {}).then(
              function(cpTreeCfg) {
                return angular.extend(result, cpTreeCfg);
              }
            );
          }
        );
      },

      saveWorkflow: saveWorkflow,

      getCommonCfg: getCommonCfg,

      getConfig: getConfig,

      getValue: getValue,

      setWorkflows: setWorkflows
    }
  });
