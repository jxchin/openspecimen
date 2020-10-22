
angular.module('os.biospecimen.participant.list', ['os.biospecimen.models'])
  .controller('ParticipantListCtrl', function(
    $scope, $state, cp, twoStepReg, mobileDataEntryEnabled,
    ParticipantsHolder, PluginReg, DeleteUtil, CollectionProtocolRegistration) {

    var ctrl = this;

    function init() {
      $scope.cpId = cp.id;
      $scope.ctx = {
        params: {
          listName: 'participant-list-view',
          objectId: cp.id
        },
        emptyState: {
          loadingMessage: 'participant.loading_list',
          emptyMessage: 'participant.empty_list'
        }
      };

      angular.extend($scope.listViewCtx, {
        twoStepReg: twoStepReg,
        listName: 'participant.list',
        ctrl: ctrl,
        headerButtonsTmpl: 'modules/biospecimen/participant/register-button.html',
        headerActionsTmpl: 'modules/biospecimen/participant/list-pager.html',
        showPrimaryBtnDd: !!cp.bulkPartRegEnabled || (PluginReg.getTmpls('participant-list', 'primary-button').length > 0),
        mobileDataEntryEnabled: mobileDataEntryEnabled
      });
    }

    $scope.showParticipant = function(row) {
      $state.go('participant-detail.overview', {cprId: row.hidden.cprId});
    };

    $scope.setListCtrl = function(listCtrl) {
      ctrl.listCtrl = $scope.ctx.listCtrl = listCtrl;
      $scope.listViewCtx.showSearch = listCtrl.haveFilters;
      $scope.listViewCtx.pagerOpts  =  listCtrl.pagerOpts;
    }

    ctrl.bulkEdit = function() {
      var selectedRows = ctrl.listCtrl.getSelectedItems();
      var cprs = selectedRows.map(function(row) { return {id: +row.hidden.cprId}; });
      ParticipantsHolder.setParticipants(cprs);
      $state.go('participant-bulk-edit');
    }

    ctrl.deleteParticipants = function() {
      var selectedRows = ctrl.listCtrl.getSelectedItems();
      var cprIds = selectedRows.map(function(row) { return +row.hidden.cprId; });

      var opts = {
        confirmDelete:  'participant.delete_participants',
        successMessage: 'participant.participants_deleted',
        pendingMessage: 'participant.participants_delete_pending',
        onBulkDeletion: function() { ctrl.listCtrl.loadList(); },
        askReason:      true
      }

      DeleteUtil.bulkDelete({bulkDelete: CollectionProtocolRegistration.bulkDelete}, cprIds, opts);
    }

    init();
  });
