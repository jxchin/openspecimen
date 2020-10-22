angular.module('os.administrative.shipment.receive', ['os.administrative.models'])
  .controller('ShipmentReceiveCtrl', function(
    $scope, $state, shipment, shipmentItems, isSpmnRelabelingAllowed, ShipmentUtil, Specimen, Container) {

    function init() {
      $scope.ctx = {
        relabelSpmns: isSpmnRelabelingAllowed,
        state: shipment.status == 'Shipped' ? 'RECV_SHIPMENT' : 'RECV_EDIT'
      };

      var attrs = getItemAttrs();
      angular.forEach(shipmentItems,
        function(item) {
          item[attrs.itemKey] = attrs.newItem(item[attrs.itemKey]);
        }
      );
      shipment[attrs.collName] = shipmentItems;

      $scope.shipment = shipment;
      $scope.spmnShipment = shipment.isSpecimenShipment();
      if (!shipment.receivedDate) {
        shipment.receivedDate = new Date();
      }
      
      showOrHidePpidAndExtIds();
    }

    function getItemAttrs() {
      if (shipment.isSpecimenShipment()) {
        return {collName: 'shipmentSpmns', itemKey: 'specimen', newItem: function(i) { return new Specimen(i) }};
      } else {
        return {collName: 'shipmentContainers', itemKey: 'container', newItem: function(i) { return new Container(i) }};
      }
    }

    function showOrHidePpidAndExtIds() {
      if (!shipment.isSpecimenShipment()) {
        return;
      }

      var result = ShipmentUtil.hasPpidAndExtIds(shipment.shipmentSpmns);
      angular.extend($scope.ctx, result);
    }

    $scope.passThrough = function() {
      return true;
    }

    //
    // initSpmnOpts is used during shipment to allow users select 
    // specimens that are suitable for shipment. No such thing exists
    // during receive; therefore it is assigned to behave same way
    // as pass through.
    //
    $scope.initSpmnOpts = $scope.passThrough;

    $scope.receive = function() {
      var shipment = angular.copy($scope.shipment);
      shipment.status = "Received";
      shipment.$saveOrUpdate().then(
        function(resp) {
          $state.go('shipment-detail.overview', {shipmentId: resp.id});
        }
      );
    }
    
    $scope.applyFirstLocationToAll = function() {
      var attrs = getItemAttrs();
      var location = shipment[attrs.collName][0][attrs.itemKey].storageLocation;
      if (!location.name) {
        return;
      }

      angular.forEach(shipment[attrs.collName],
        function(item, idx) {
          if (idx == 0) {
            return;
          }

          item[attrs.itemKey].storageLocation = {name: location.name, mode: location.mode};
        }
      );
    }

    $scope.copyFirstQualityToAll = function() {
      var attrs = getItemAttrs();
      var quality = shipment[attrs.collName][0].receivedQuality;

      angular.forEach(shipment[attrs.collName],
        function(item) {
          item.receivedQuality = quality;
        }
      );
    }

    init();
  });
