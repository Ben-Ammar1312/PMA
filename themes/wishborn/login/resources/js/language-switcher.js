document.addEventListener('DOMContentLoaded', function () {
    var languageSelect = document.getElementById('kc-locale-dropdown');

    if (languageSelect) {
        languageSelect.addEventListener('change', function () {
            var selectedLocale = this.value;
            var url = new URL(window.location.href);
            url.searchParams.set('kc_locale', selectedLocale);
            window.location.href = url.toString();
        });
    }
});