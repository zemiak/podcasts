var DataTablesExt = {
    editClick: function() {
        var tt = TableTools.fnGetInstance("grid");
        var rows = tt.fnGetSelectedData();
        var id = 1 !== rows.length ? null : rows[0].id;
    
        if (null === id) {
            $("#select-one").show();
        } else {
            window.location = "edit.xhtml?id=" + id;
        }
    },
    
    editDoubleClick: function(element) {
        var id = $(element).children().first().text();
        window.location = "edit.xhtml?id=" + id;
    }
};
