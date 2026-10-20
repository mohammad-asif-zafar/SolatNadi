// SolatNadi Website Interactive Script

document.addEventListener('DOMContentLoaded', () => {
    // 1. Theme Management (Light by default, Dark optional)
    const themeToggleBtn = document.getElementById('themeToggleBtn');

    function applyTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem('solatnadi-theme', theme);
        if (themeToggleBtn) {
            themeToggleBtn.textContent = theme === 'dark' ? '☀️' : '🌙';
            themeToggleBtn.setAttribute('title', theme === 'dark' ? 'Switch to Light Mode' : 'Switch to Dark Mode');
        }
    }

    // Default to Light Mode unless explicitly saved as dark in localStorage
    const savedTheme = localStorage.getItem('solatnadi-theme') || 'light';
    applyTheme(savedTheme);

    if (themeToggleBtn) {
        themeToggleBtn.addEventListener('click', () => {
            const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
            applyTheme(newTheme);
        });
    }

    // 2. Mobile Menu Toggle
    const mobileToggle = document.getElementById('mobileNavToggle');
    const navLinks = document.getElementById('navLinks');

    if (mobileToggle && navLinks) {
        mobileToggle.addEventListener('click', () => {
            navLinks.classList.toggle('active');
            const isExpanded = navLinks.classList.contains('active');
            mobileToggle.setAttribute('aria-expanded', isExpanded);
        });
    }

    // 3. Contact Form Validation & Submission Feedback
    const contactForm = document.getElementById('contactForm');
    const formFeedback = document.getElementById('formFeedback');

    if (contactForm && formFeedback) {
        contactForm.addEventListener('submit', (e) => {
            e.preventDefault();

            const name = document.getElementById('senderName')?.value.trim();
            const email = document.getElementById('senderEmail')?.value.trim();
            const subject = document.getElementById('emailSubject')?.value.trim();
            const message = document.getElementById('emailMessage')?.value.trim();

            if (!name || !email || !message) {
                formFeedback.className = 'form-feedback error';
                formFeedback.textContent = 'Please fill in all required fields (Name, Email, Message).';
                return;
            }

            const mailtoSubject = encodeURIComponent(`[SolatNadi Support] ${subject || 'General Inquiry'}`);
            const mailtoBody = encodeURIComponent(`Name: ${name}\nEmail: ${email}\n\nMessage:\n${message}`);
            const mailtoUrl = `mailto:support@solatnadi.app?subject=${mailtoSubject}&body=${mailtoBody}`;

            formFeedback.className = 'form-feedback success';
            formFeedback.innerHTML = `Thank you, <strong>${name}</strong>! Opening your email client to send message to mohammadasifzafar000@gmail.com....`;

            setTimeout(() => {
                window.location.href = mailtoUrl;
                contactForm.reset();
            }, 800);
        });
    }
});
