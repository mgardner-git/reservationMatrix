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
		/**
		 * $scope.beginDate and $scope.endDate track the inner date range, the part that is visible within the viewport.
		 * $scope.outerBeginDate and $scope.outerEndDate track the outer date range.  This is tracked so that we can load a larger date range
		 * then is currently visible.  This way, the user can scroll left and right without having to immediately reload data.  After each 
		 * left or right scroll, a new outer range is loaded. The inner date range should be maintained as one third of the outer date range, with 
		 * each of the other 2/3rds being to the left and right.
		 */
		controller: function($scope,$timeout, $http){			
			$scope.dateRange = new Array();
			$scope.outerBeginDate = moment();
			$scope.outerEndDate = moment();
			
			$scope.getReservables = function(){
				var data = {
						beginDate: $scope.outerBeginDate.valueOf(),
						endDate: $scope.outerEndDate.valueOf()
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
					            
	            diff = end.diff(begin, "days");
	            begin = begin.subtract(diff,"days");
	            end = end.add(diff,"days");
	            $scope.outerBeginDate = begin;
	            $scope.outerEndDate = end;
	            
	            var indexMoment = moment(begin).startOf("day");
	            $scope.dateRange = new Array();
	            $scope.dateRange.push(moment(indexMoment));
	            for(var i = 0; i < diff*3; i++) {
	            	indexMoment.add("1","d");
	            	$scope.dateRange.push(moment(indexMoment));
	            }
	            $timeout(function(){  
	    			/*
	            	var visibleWidth = jQuery("#wrapper").width();
	    			var dateRangeCells = jQuery("#wrapper tr").last().find("td");
	    			
	    			var leftMostVisibleCell = jQuery(dateRangeCells[Math.floor(dateRangeCells.length/3)]);
	    			var rightIndex = Math.floor(2*(dateRangeCells.length/3));
	    			var rightMostVisibleCell = jQuery(dateRangeCells[rightIndex]);
	    			
	    			var scrollDistance = leftMostVisibleCell.offset().left - jQuery("#inner").offset().left;
	    			jQuery("#inner").scrollLeft(scrollDistance);
	    			var visibleCellWidth = Math.floor(visibleWidth/(dateRangeCells.length/3));
	    			var entireWidth = visibleCellWidth * dateRangeCells.length;
	    			jQuery("#matrix").css("width",entireWidth + "px");
	    			*/
	    				    			
	            });
	            

	            $scope.getReservations();
			},true);
			
			$scope.getReservations = function(){
	            var params = {
		            	params: {
		            		beginDate: $scope.outerBeginDate.valueOf(),
		            		endDate: $scope.outerEndDate.valueOf()
		            	}
		        }
				$http.get("ReservationsServlet", params).
	            success(function(data, status, headers, config) {
					$scope.reservations = data;					
				});
			};
			
			$scope.format = function(date){
				return (date.month()+1) + "/" + date.date() + "/" + date.year();
			}
			$scope.isReserved = function(date, reservable){
				if ($scope.reservations){
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
				}
				return false;
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
									alert("Can't reserve that. Unit " + reservable.name + " is already reserved on " + $scope.format(indexDate));
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