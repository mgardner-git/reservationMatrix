App.directive("dateDirective",function(){
	return{ 
		restrict: "E",
		template:'<input type="text" class="{{cssClass}}" tabIndex="{{tabIndex}}" ng-blur="blur()"></input>',
		scope:{
			model: "=",
			cssClass: "@",
			tabIndex: "@",
			blur: "&",
			fieldName: "@",
			placeholder: "@"
		},
		link: function($scope, element, attrs){
			var inputNode = jQuery(element).find("input");
			var that = jQuery(element).find("input");
			
			that.datepicker({		
				dateFormat: "mm/dd/yy",
				onSelect: function(event, ui) {
					$scope.$apply(function(){
						$scope.model = that.datepicker( "getDate" );
					});					
				}
			});
			$scope.$watch("model", function(){
				var format = ($scope.model.getMonth()+1) + "/" + $scope.model.getDate() + "/" + $scope.model.getFullYear();
				var inputNode = jQuery(element).find("input");
				inputNode.val(format);								
			},true);

		} ,
		controller: function($scope){

		}
	};
});