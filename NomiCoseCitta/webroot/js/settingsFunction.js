function init() {
   var url = new URL(document.URL);
   var name = url.searchParams.get("name");
   console.log(name);
}