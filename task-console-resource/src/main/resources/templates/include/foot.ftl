<#ftl encoding="utf-8">
<footer class="panel-footer" style="background-color: lightgray;">
    <div class="container">
        <div class="row">
            <div class="col-md-3">
                <p style="color:white">版权所有： ©2018  铁甲 - Easy Task </p>
                <p style="color:white">地址：北京市朝阳区  </p>
            </div>
            <div class="cod-md-9">
                <div style="">
                    <span style="color:white;font-size:18px;">Easy Task</span>
                    <span style="color:white;margin-left: 20px;float: right"></span>
                </div>
            </div>


        </div>


    </div>
    <script>

        var rootApp = angular.module('rootApp',[]);
        rootApp.run(function ($templateCache) {
            $templateCache.put('pagebarTemplateKey',
                    '<div class="pagebar" ng-show="totalresultsize" style="background-color: #EEE;height: 74px;padding: 0 20px">' +
                    '<div class="text-primary" style="float: left;line-height: 74px;">每页显示数量：' +
                    '<select class="pageSizeSelect" ng-model="pagesize">' +
                    '<option ng-repeat="obj in pagesizelist" value="{{obj}}">{{obj}}</option>' +
                    '</select>' +
                    '<span> 总共 {{totalresultsize}} 条</span>' +
                    '</div>' +
                    '<div style="float: right">' +
                    '<ul class="pagination">' +
                    '<li ng-click="pre()" ng-class="{true: \'disabled\', false: \'\'}[curpagination <= 1]">' +
                    '<a href="javascript:void(0)" aria-label="Previous">' +
                    '<span aria-hidden="true"><strong>&lt; </strong>上一页</span>' +
                    '</a>' +
                    '</li>' +
                    '<li ng-repeat="pg in pageIndexList" ng-class="{ \'active\': pg == curpagination, \'disabled\' : pg == \' ...\' || pg == \'... \'}"  ng-click="loadPage(pg)">' +
                    '<a href="javascript:void(0)">{{pg}}</a>' +
                    '</li>' +
                    '<li ng-click="next()" ng-class="{true: \'disabled\', false: \'\'}[curpagination >= totalpagination]">' +
                    '<a href="javascript:void(0)" aria-label="Next">' +
                    '<span aria-hidden="true">下一页<strong> &gt;</strong></span>' +
                    '</a>' +
                    '</li>' +
                    '</li>' +
                    '<li ng-show="totalpagination > paginationcount">' +
                    '<input ng-model="jp" ng-keyup="keyupJp()" style="width: 76px;height: 34px;margin-left: 2px;margin-right: 2px;">' +
                    '<button ng-click="jump()" class="btn-primary" style="width: 70px;height: 34px;">GO</button>' +
                    '</li>' +
                    '</ul>' +
                    '</div>' +
                    '</div>' +
                    '<div ng-show="!totalresultsize" class="alert alert-danger" role="alert" style="text-align: center">无数据</div>');
        });

        rootApp.directive("pagebar", ['$templateCache', function ($templateCache) {
            return {
                restrict: 'E',
                scope: {
                    curpagination: '=', //当前页页码
                    pagesize: '=',  //每页显示数据的量
                    totalpagination: '=',   //总共多少页
                    totalresultsize: '=',   //总共多少数据
                    pagesizelist: '=?', //每个分页的数据量的列表
                    paginationcount: '=?'    //显示多少页码
                },
                template: $templateCache.get('pagebarTemplateKey'),
                link: function (scope, element, attr) {
                    var DEFAULT = {
                        PAGE_SIZE_LIST: [20, 40, 80, 100, 150, 200],   //默认每个分页的数据量的列表
                        PAGINATION_COUNT: 5    //显示多少页码
                    }
                    if (!attr.pagesizelist || attr.pagesizelist.length == 0) {
                        scope.pagesizelist = DEFAULT.PAGE_SIZE_LIST;
                    }
                    if (!attr.paginationcount || attr.paginationcount == 0) {
                        scope.paginationcount = DEFAULT.PAGINATION_COUNT;
                    }
                    scope.leftAndRight = Math.floor((scope.paginationcount - 2) / 2);    //选中页面，左右两边显示多少页


                    scope.pre = function () {
                        if (this.curpagination - 1 <= 0) {
                            return;
                        }
                        this.curpagination--;
                    };
                    scope.next = function () {
                        if (!this.totalpagination || this.curpagination >= this.totalpagination) {
                            return;
                        }
                        this.curpagination++;
                    };
                    scope.jump = function () {
                        var intPageIndex = parseInt(scope.jp);
                        if (isNaN(intPageIndex)) {
                            return;
                        }
                        if (intPageIndex <= 0) {
                            scope.curpagination = 1;
                            scope.jp = 1;
                        } else if (intPageIndex > scope.totalpagination) {
                            scope.jp = scope.totalpagination;
                            scope.curpagination = scope.totalpagination;
                        } else {
                            scope.curpagination = intPageIndex;
                        }
                    };
                    scope.keyupJp = function () {
                        scope.jp = scope.jp.replace(/[^\d]/g, '');
                        var intPageIndex = parseInt(scope.jp);
                        if (isNaN(intPageIndex)) {
                            return;
                        }
                        if (intPageIndex <= 0) {
                            scope.jp = 1;
                        } else if (intPageIndex > scope.totalpagination) {
                            scope.jp = scope.totalpagination;
                        } else {
                            scope.jp = intPageIndex;
                        }
                    };
                    scope.loadPage = function (pageIndex) {
                        scope.curpagination = pageIndex;
                    };
                    scope.pageIndexList = [];
                    scope.$watchCollection('curpagination', function (newValue, oldValue, $scope) {
                        changePagination(newValue, oldValue, $scope);
                    });
                    scope.$watchCollection('totalpagination', function (newValue, oldValue, $scope) {
                        changePagination(newValue, oldValue, $scope);
                    });
                    var changePagination = function (newValue, oldValue, $scope) {
                        if (newValue === oldValue) {
                            return;
                        }
                        scope.pageIndexList = [];
                        //1、不需要分页
                        if (!$scope.totalpagination || $scope.totalpagination <= 1) {
                            //1.1、添加第一页
                            scope.pageIndexList.push(1);
                            return;
                        }
                        //2、页数少于默认显示页数
                        if ($scope.totalpagination <= $scope.paginationcount) {
                            //2.1、添加第一页
                            scope.pageIndexList.push(1);
                            //2.2、添加中间页
                            if ($scope.totalpagination >= 3) {
                                for (var i = 2; i < $scope.totalpagination; i++) {
                                    scope.pageIndexList.push(i);
                                }
                            }
                            //2.3、添加最后一页
                            scope.pageIndexList.push($scope.totalpagination);
                        } else {
                            //3、页数多
                            //3.1、添加第一页
                            scope.pageIndexList.push(1);
                            scope.pageIndexList.push("... ");
                            //3.2、添加中间页
                            //3.2.1、添加选中页左边的页面
                            for (var i = $scope.curpagination - scope.leftAndRight; i < $scope.curpagination; i++) {
                                if (i <= 1) {
                                    continue;
                                }
                                scope.pageIndexList.push(i);
                            }
                            //3.2.2、添加选中页
                            if ($scope.curpagination != 1 && $scope.curpagination != $scope.totalpagination) {
                                scope.pageIndexList.push($scope.curpagination);
                            }
                            //3.2.3、添加选中页右边的页面
                            for (var i = $scope.curpagination + 1; i <= $scope.curpagination + scope.leftAndRight; i++) {
                                if (i >= $scope.totalpagination) {
                                    continue;
                                }
                                scope.pageIndexList.push(i);
                            }
                            //3.3、整理
                            if (scope.pageIndexList[scope.pageIndexList.length - 1] + 1 != $scope.totalpagination) {
                                scope.pageIndexList.push(" ...");
                            }
                            if (scope.pageIndexList[0] + 1 == scope.pageIndexList[2]) {
                                scope.pageIndexList.splice(1, 1);
                            }
                            //3.4、添加最后一页
                            scope.pageIndexList.push($scope.totalpagination);
                        }

                    };
                }
            }
        }]);

    </script>
</footer>