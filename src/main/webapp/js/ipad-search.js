function searchUrl() {
    var url = "searchResults.xhtml?query=" + encodeURIComponent($("#searchQuery").val());
    return url;
}

$("document").ready(function(){
    $("#searchQuery").keyup(function(e) {
        var charCode = (typeof e.which === "number") ? e.which : e.keyCode;
        if (charCode === 13) {
            e.preventDefault();
            window.location = searchUrl();
        }
    });
});
