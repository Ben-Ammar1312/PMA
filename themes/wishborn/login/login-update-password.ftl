<#ftl output_format="HTML">
<#import "template.ftl" as kc>
<#setting boolean_format="c">

<!DOCTYPE html>
<html lang="${locale}"><#if direction??>dir="${direction}"</#if>
<head>
    <meta charset="utf-8"/>
    <title>${msg("updatePasswordTitle")} • ${realm.displayName!realm.name}</title>

    <!-- Tailwind + Geist bundle -->
    <link rel="stylesheet" href="${url.resourcesPath}/css/styles.css">
    <!-- Inter fallback (optional) -->
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap">

    <style>
        html,body { background:#EEEEEE; font-family:'Geist','Inter',sans-serif }
        .fade-enter { opacity:0; transform:scale(.95) }
        .fade-enter-active { opacity:1; transform:scale(1); transition:all .15s ease }
    </style>
</head>

<body class="min-h-screen flex items-center justify-center">

<div class="bg-white border border-gray-200 shadow-sm rounded-xl
            w-full max-w-md p-8 flex flex-col gap-6 relative" style="background:#FFFFFF;">

    <!-- Language dropdown -->
    <#if realm.internationalizationEnabled && (locale.supported?size > 1)>
        <div id="kc-lang-switcher" class="absolute top-4 right-4">
            <button id="kc-lang-btn"
                    class="p-2 rounded-full bg-gray-100 hover:bg-gray-200
                     focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
                     viewBox="0 0 24 24" fill="none" stroke="currentColor"
                     stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                     class="lucide lucide-languages-icon"><path d="m5 8 6 6"/><path d="m4 14 6-6 2-3"/><path d="M2 5h12"/><path d="M7 2h1"/><path d="m22 22-5-10-5 10"/><path d="M14 18h6"/></svg>
            </button>
            <ul id="kc-lang-menu"
                class="hidden fade-enter absolute right-0 mt-2 w-40 origin-top-right bg-white
                 border border-gray-200 rounded-md shadow-lg z-50">
                <#list locale.supported as l>
                    <li class="bg-white first:rounded-t-md last:rounded-b-md">
                        <a href="${l.url}"
                           class="block px-3 py-2 text-sm bg-white hover:bg-gray-100
                 <#if l.languageTag == locale>font-semibold text-indigo-600</#if>">
                            ${l.label}
                        </a>
                    </li>
                </#list>
            </ul>
        </div>

        <script>
            (() => {
                const btn = document.getElementById('kc-lang-btn');
                const menu = document.getElementById('kc-lang-menu');
                btn?.addEventListener('click', e => {
                    e.preventDefault();
                    menu.classList.toggle('hidden');
                    if (!menu.classList.contains('hidden')) {
                        menu.classList.add('fade-enter');
                        requestAnimationFrame(()=>menu.classList.add('fade-enter-active'));
                        setTimeout(()=>menu.classList.remove('fade-enter','fade-enter-active'),150);
                    }
                });
                document.addEventListener('click', e => {
                    if (!btn?.contains(e.target) && !menu?.contains(e.target)) menu.classList.add('hidden');
                });
            })();
        </script>
    </#if>

    <!-- Logo -->
    <div class="flex justify-center -mt-2 mb-2">
        <img src="${url.resourcesPath}/img/logoSmallNoBg.png" alt="Company logo" width="220" height="100"/>
    </div>

    <!-- Heading -->
    <h1 class="text-2xl text-center text-[#465573] font-bold mb-2">
        ${msg("updatePasswordTitle")}
    </h1>

    <!-- Helper text (Keycloak’s default message if present) -->
    <#if message?? && message.summary?has_content>
        <div class="text-center text-sm text-[#4A4A4A]">${message.summary?no_esc}</div>
    </#if>

    <!-- Update Password Form -->
    <form id="kc-passwd-update-form" action="${url.loginAction}" method="post" class="flex flex-col gap-4"
          onsubmit="login && (login.disabled = true); return true;">

        <!-- New password -->
        <div>
            <label for="password-new" class="text-[#4A4A4A] font-semibold">${msg("passwordNew")}</label>
            <div class="relative" dir="ltr">
                <!-- lock icon -->
                <svg xmlns="http://www.w3.org/2000/svg"
                     class="w-5 h-5 text-gray-500 absolute left-3 top-1/2 -translate-y-1/2"
                     viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <rect x="3" y="11" width="18" height="10" rx="2" stroke-width="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke-width="2"/>
                </svg>

                <input type="password" id="password-new" name="password-new"
                       class="pl-12 w-full border border-gray-300 rounded-md py-2
                      focus:border-[#6985C0] focus:ring-2 focus:ring-[#6985C0]"
                       autocomplete="new-password" autofocus
                       aria-invalid="<#if messagesPerField.existsError('password','password-confirm')>true</#if>"/>

                <!-- show/hide button (uses passwordVisibility.js) -->
                <button type="button" aria-label="${msg('showPassword')}"
                        aria-controls="password-new" data-password-toggle
                        data-icon-show="i-eye" data-icon-hide="i-eye-off"
                        data-label-show="${msg('showPassword')}" data-label-hide="${msg('hidePassword')}"
                        class="absolute right-2 top-1/2 -translate-y-1/2 px-2 text-gray-600">
                    <i class="i-eye" aria-hidden="true"></i>
                </button>
            </div>

            <#if messagesPerField.existsError('password')>
                <span id="input-error-password" class="text-sm text-red-600 mt-1" aria-live="polite">
          ${kcSanitize(messagesPerField.get('password'))?no_esc}
        </span>
            </#if>
        </div>

        <!-- Confirm password -->
        <div>
            <label for="password-confirm" class="text-[#4A4A4A] font-semibold">${msg("passwordConfirm")}</label>
            <div class="relative" dir="ltr">
                <!-- lock-check icon -->
                <svg xmlns="http://www.w3.org/2000/svg"
                     class="w-5 h-5 text-gray-500 absolute left-3 top-1/2 -translate-y-1/2"
                     viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <rect x="3" y="11" width="18" height="10" rx="2" stroke-width="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke-width="2"/>
                    <path d="m9 16 2 2 4-4" stroke-width="2"/>
                </svg>

                <input type="password" id="password-confirm" name="password-confirm"
                       class="pl-12 w-full border border-gray-300 rounded-md py-2
                      focus:border-[#6985C0] focus:ring-2 focus:ring-[#6985C0]"
                       autocomplete="new-password"
                       aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>"/>

                <button type="button" aria-label="${msg('showPassword')}"
                        aria-controls="password-confirm" data-password-toggle
                        data-icon-show="i-eye" data-icon-hide="i-eye-off"
                        data-label-show="${msg('showPassword')}" data-label-hide="${msg('hidePassword')}"
                        class="absolute right-2 top-1/2 -translate-y-1/2 px-2 text-gray-600">
                    <i class="i-eye" aria-hidden="true"></i>
                </button>
            </div>

            <#if messagesPerField.existsError('password-confirm')>
                <span id="input-error-password-confirm" class="text-sm text-red-600 mt-1" aria-live="polite">
          ${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}
        </span>
            </#if>
        </div>

        <!-- Logout other sessions -->
        <div class="flex items-center gap-2 mt-1">
            <input id="logout-other-sessions" name="logout-other-sessions" type="checkbox"
                   class="h-4 w-4 border-gray-300 rounded focus:ring-[#6985C0]">
            <label for="logout-other-sessions" class="text-sm text-[#4A4A4A]">
                ${msg("logoutOtherSessions")}
            </label>
        </div>

        <!-- Buttons -->
        <div class="flex justify-center gap-3 mt-2">
            <#if isAppInitiatedAction??>
                <input name="login" type="submit" value="${msg('doSubmit')}"
                       class="w-40 bg-[#6985C0] hover:bg-[#637498] text-white font-semibold rounded-lg py-2 transition-colors"/>
                <button type="submit" name="cancel-aia" value="true"
                        class="w-40 bg-white border border-gray-300 text-[#465573] rounded-lg py-2 hover:bg-gray-50">
                    ${msg("doCancel")}
                </button>
            <#else>
                <input name="login" type="submit" value="${msg('doSubmit')}"
                       class="w-40 bg-[#6985C0] hover:bg-[#637498] text-white font-semibold rounded-lg py-2 transition-colors"/>
            </#if>
        </div>
    </form>
</div>

<!-- Keep KC’s password visibility JS -->
<script type="module" src="${url.resourcesPath}/js/passwordVisibility.js"></script>
</body>
</html>