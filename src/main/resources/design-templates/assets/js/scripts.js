jQuery(document).ready(function() {
  /*
        Background slideshow
    */
  $(".top-content").backstretch("assets/img/backgrounds/bg-ugal.jpg");

  /*
        Wow
    */
  new WOW().init();

  /*
        Search form
    */
  $(".navbar-search-button .search-button").on("click", function(e) {
    e.preventDefault();
    $(this).blur();
    $(".navbar-header, .navbar-menu-items, .navbar-search-form").toggleClass(
      "disabled"
    );
    $(".navbar-search-form input.search")
      .val("")
      .focus();
  });
});
