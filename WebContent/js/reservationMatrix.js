App.directive("reservationMatrix",function(){
	return{
		restrict: "E",
		templateUrl:"templates/reservationMatrix.html",
		scope:{
			beginDate: "=",
			endDate: "=",
			reservables: "=",
			reservations: "="
		},
		controller: function($scope,$timeout, $http){			
			$scope.dateRange = new Array();
			
			$scope.getReservables = function(){
				var data = {
						beginDate: $scope.beginDate.getTime(),
						endDate: $scope.endDate.getTime()
				}
				$http.get('ReservablesServlet', data).
				success(function(data, status, headers, config) {		

					$scope.reservables = data;
				});
			};
			$scope.getReservables();
			
			$scope.createReservation = function(reservation){
				var postData = {
						//moment.valueOf returns milliseconds
						beginDate: reservation.beginDate.valueOf(),
						endDate: reservation.endDate.valueOf(),
						reservables: reservation.reservables
				};
				$http.post("ReservationsServlet", postData).
				success(function(data,status,headers,config){
					$scope.getReservations();
					jQuery("#matrix td.spot").removeClass("ui-selected");
				});
			};
			$scope.$watch("[beginDate.getTime(),endDate.getTime()]", function(oldValue, newValue){				
				var begin=moment($scope.beginDate);
				var end=moment($scope.endDate);			
				$scope.dateRange = new Array();	            
	            diff = end.diff(begin, 'days');

	            $scope.dateRange.push(moment(begin).startOf("day"));
	            for(var i = 0; i < diff; i++) {
	            	$scope.dateRange.push(moment(begin.add(1,'d')));
	            }
	            $timeout(function(){
	    			var width = jQuery("#matrix").width() + 80;	    			
	    			jQuery("#inner").css("width", width + "px");

	            });
	            

	            $scope.getReservations();
			},true);
			
			$scope.getReservations = function(){
	            var params = {
		            	params: {
		            		beginDate: $scope.beginDate.getTime(),
		            		endDate: $scope.endDate.getTime()
		            	}
		        }
				$http.get("ReservationsServlet", params).
	            success(function(data, status, headers, config) {
					$scope.reservations = data;
				});
			};
			
			$scope.format = function(date){
				return date.month() + "/" + date.date() + "/" + date.year();
			}
			$scope.isReserved = function(date, reservable){
				var checkDate = moment(date);
				for (var index=0; index < $scope.reservations.length; index++){
					var checkReservation = $scope.reservations[index];
					var checkStart = moment(checkReservation.beginDate);
					var checkEnd = moment(checkReservation.endDate);
					var matchedReservable = false;					
					for (var index2=0; index2 < checkReservation.reservables.length; index2++){
						if (checkReservation.reservables[index2].id == reservable.id){
							matchedReservable=true;
						}
					}
					if (checkDate >= checkStart && checkDate <= checkEnd && matchedReservable){
						return true;
					}
				}
			};
			$scope.left = function(){
				jQuery("#wrapper").kinetic("start", { velocity: -95 });
			}
			$scope.right = function(){
				jQuery("#wrapper").kinetic("start", { velocity: 95 });
			}

			
		}, 
		link: function($scope){
			jQuery("#wrapper").kinetic({
			    filterTarget: function(target, e){
			    	target = jQuery(target);
			    	id = target.attr("id");
			    	if (id != "inner"){
			    		return false;
			    	}
			    }
			});
			jQuery("#matrix").selectable({
				filter: "td.spot",
				stop: function(){
					var rows = jQuery("#matrix tr.reservable");
					var newReservation = {reservables: new Array()};
					for (var index=0; index < rows.length; index++){
						var row = rows.get(index);
						row = jQuery(row);
						var columns = row.find("td.spot");
						var reservable = $scope.reservables[index];
						var beginDate = null;
						var endDate = null;
						for (var index2 = 0; index2 < columns.length; index2++){
							var column = columns.get(index2);
							column = jQuery(column);
							if (column.hasClass("ui-selected")){
								var timeSlot = $scope.dateRange[index2];
								if (beginDate == null){
									beginDate = timeSlot;
								}
								endDate = timeSlot;
							}
						}
						if (beginDate != null && endDate != null){
							newReservation.beginDate = beginDate;
							newReservation.endDate = endDate;
							var ed = moment(endDate);
							for (var indexDate = moment(beginDate); indexDate <= ed; indexDate.add(1,"d")){
								if ($scope.isReserved(indexDate, reservable)){
									alert("Can't reservate that. Unit " + reservable.name + " is already reserved on " + $scope.format(indexDate));
									jQuery("#matrix td.spot").removeClass("ui-selected");
									return false;
								}
							}
							newReservation.reservables.push(reservable);
						}
					}
					$scope.createReservation(newReservation);					
				}
			});
		}
	}
});