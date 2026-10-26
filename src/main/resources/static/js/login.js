document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault(); // Prevent default HTML page reload

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                userEmail: email,
                userPassword: password
            })
        });

        if (response.ok) {
            // Since the backend redirects or returns success, navigate to app.html
            window.location.href = '/app/internacao.html';
        } else {
            const err = await response.json();
            alert(err.error || 'Login failed');
        }
    } catch (error) {
        console.error('Error during login:', error);
    }
});
