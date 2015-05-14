angular.module('os.biospecimen.specimenlist.addedit', ['os.biospecimen.models'])
  .controller('AddEditSpecimenListCtrl', function(
    $scope, $state, list, SpecimenList, SpecimensHolder) {
 
    function init() { 
      $scope.list = list;
      $scope.list.specimens = SpecimensHolder.getSpecimens() || list.specimens;
      $scope.isQueryOrSpecimenPage =  SpecimensHolder.getSpecimens() != undefined;
    }

    $scope.saveOrUpdateList = function() {
      var sharedWith = $scope.list.sharedWith.map(function(user) { return {id: user.id} });
      var specimenList =  new SpecimenList({
        id: $scope.list.id,
        name: $scope.list.name,
        sharedWith: sharedWith,
        specimens: $scope.list.specimens
      }); 
 
      specimenList.$saveOrUpdate().then(
        function(savedList) {
          if ($scope.isQueryOrSpecimenPage) {
            $scope.back();
          } else {
           $state.go('specimen-list', {listId: savedList.id});
          }
        }
      );
    }
    
    $scope.deleteList = function() {
      $scope.list.$remove().then(
        function() {
          $state.go('specimen-list');
        }
      );
    };

    init();
  }
);
