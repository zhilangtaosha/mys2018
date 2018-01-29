var myPage;

function Node(obj) {
	this.skillId = ko.observable(obj.skillId); 
	this.skillName = ko.observable(obj.skillName); 
	this.materialId = ko.observable(obj.materialId); 
	this.isClock = ko.observable(obj.isClock); 
	this.classs = ko.observable(obj.classs); 
	this.status = ko.observable(obj.status); 
	this.createdBy = ko.observable(obj.createdBy); 
	this.createdDate = ko.observable(obj.createdDate); 
	this.updatedBy = ko.observable(obj.updatedBy); 
	this.updatedDate = ko.observable(obj.updatedDate); 
}

//定义ViewModel对象
var ClockSkillViewModel = function () {  
	var self=this;
    //添加动态监视数组对象
    self.clockSkillList = ko.observableArray([]);
    
    var myurl=homeUrl+"/clockskills";
    if(getQueryString('page')!=null){
    	myurl+="?pageNo="+getQueryString('page');
    }
    	
    //初始化数据
    $.getJSON(myurl,function(result){
		var mappedTasks = $.map(result.data, function(item) { return new Node(item) });  
	    self.clockSkillList(mappedTasks);
	    myPage = result.page;
	    bindPage();
	    
	    $("table tbody td .tomodify").bind(function(){
	    	ChangeUrl('./clock/ClockSkill.html?action=Edit&id='+$(this).attr('data'));
	    });
	});
	
	//搜索
	self.search = function(obj) {
		$.getJSON(homeUrl+"/clockskills?attendanceName="+$("txtKeywords").val(),function(result){
			var mappedTasks = $.map(result.data, function(item) { return new Node(item) });  
		    self.clockSkillList(mappedTasks);
		    myPage = result.page;
		    bindPage();
		});
    };
    
    //新增
    self.add = function(obj) {
    	ChangeUrl('./clock/ClockSkill.html?action=Add');
    };
    
    //修改
    self.modify=function(obj){
    	ChangeUrl('./clock/ClockSkill.html?action=Edit&id='+obj.skillId());
    };
    
    //删除
    self.delete=function(obj){
    	parent.dialog({
            title: '提示',
            content: '确定要删除该记录！',
            okValue: '确定',
            ok: function () {
            	var id = obj.skillId();
            	MyAjax("DELETE",
	            	homeUrl+'/clockskill/'+id,
	            	null,
	            	function(result){
	            		location.reload();
	            	}
            	);
            }
        }).showModal();
    }
    
    //批量删除
    self.deletes = function(obj) {
    	if ($(".checkall input:checked").size() < 1) {
        	parent.dialog({
	            title: '提示',
	            content: '对不起，请选中您要操作的记录！',
	            okValue: '确定',
	            ok: function () { }
	        }).showModal();
	        return false;
	    }
	    var msg = "删除记录后不可恢复，您确定吗？";
	    parent.dialog({
	        title: '提示',
	        content: msg,
	        okValue: '确定',
	        ok: function () {
	        	$(".checkall input:checked").each(function(i){
	        		MyAjax("DELETE",
		        		homeUrl+'/clockskill/'+$(this).attr('data'),
		        		null,
		        		function(){
		        			
		        		}
	        		);
	        	});
	        	location.reload();
	        },
	        cancelValue: '取消',
	        cancel: function () { }
	    }).showModal();
    };

};

$().ready(function(){

    ko.applyBindings(new ClockSkillViewModel());

});

var bindPage =function(){
	//分页控件加载处理
    $.jqPaginator('#pagination', {
        totalPages: myPage.totalPages,
        visiblePages: myPage.limit,
        currentPage: myPage.page,
        onPageChange: function (num, type) {
            if (type != 'init') {
            	ChangeUrl('./clock/ClockSkillList.html?page=' + num);
            }
        }
    });
}
